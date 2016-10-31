using System;
using System.Linq;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography.Signing
{
    public class SoftwareBasedXmlSigningService : IXmlSigningService
    {
        // Own certificate and private key
        private X509Certificate2 _ownSigningCert;
        private RSACryptoServiceProvider _privateKey;

        // Root certificate of bank
        private X509Certificate2 _bankRootCertificate;

        public SoftwareBasedXmlSigningService(X509Certificate2 bankCertificate)
        {
            _bankRootCertificate = bankCertificate;
        }

        public SoftwareBasedXmlSigningService() : this(null)
        { }

        /// <summary>
        /// 
        /// </summary>
        public X509Certificate2 BankRootCertificate
        {
            get { return _bankRootCertificate; }
            set { _bankRootCertificate = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="signingCert"></param>
        /// <param name="privateKey"></param>
        public void SetClientCertificate(X509Certificate2 signingCert, RSACryptoServiceProvider privateKey)
        {
            _ownSigningCert = signingCert;
            _privateKey = privateKey;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="xmlDoc"></param>
        public void AddSignature(XmlDocument xmlDoc)
        {
            // These requests cannot be signed by the client
            string[] doNotSignTheseRequests = new[] {
                // The client does not have a certificate yet, and thus cannot sign the request
                "GetBankCertificateRequest", 

                // The client does not have a certificate yet, and thus cannot sign the request
                "CreateCertificateRequest"
            };

            // Read our signing certificate
            #region Find node to sign
            // Find all *Request nodes
            XmlNamespaceManager xmlns = new XmlNamespaceManager(xmlDoc.NameTable);
            var requestElements = xmlDoc.SelectNodes("//*['Request' = substring(name(), string-length(name()) - string-length('Request') + 1)]", xmlns);

            if (requestElements.Count == 0)
                return; // Nothing to sign
            if (requestElements.Count > 1)
                throw new Exception("Cannot sign multiple elements");

            XmlElement elem = requestElements[0] as XmlElement;
            if (doNotSignTheseRequests.Contains(elem.LocalName))
                return; // Skip this element
            #endregion


            // Add attribute so we can reference the element which will be signed
            elem.SetAttribute("xml:id", "Request");

            var signedXml = new SignedXmlWithId(elem);


            RSACryptoServiceProvider rsa = (RSACryptoServiceProvider)_privateKey;
            
            signedXml.SigningKey = _privateKey;


            // Constructand add keyinfo element with the used certificate
            KeyInfo keyInfo = new KeyInfo();
            var x509Data = new KeyInfoX509Data(_ownSigningCert);
            x509Data.AddIssuerSerial(_ownSigningCert.IssuerName.Name, _ownSigningCert.SerialNumber);
            keyInfo.AddClause(x509Data);
            signedXml.KeyInfo = keyInfo;

            // Specify a canonicalization method.
            signedXml.SignedInfo.CanonicalizationMethod = SignedXml.XmlDsigExcC14NTransformUrl;

            // Create a reference to be signed.
            Reference reference = new Reference();
            reference.AddTransform(new XmlDsigEnvelopedSignatureTransform());
            reference.AddTransform(new XmlDsigExcC14NTransform());

            reference.Uri = "#Request";

            // Add the reference to the SignedXml object.
            signedXml.AddReference(reference);

            // Compute the signature.
            signedXml.ComputeSignature();

            XmlElement xmlDigitalSignature = signedXml.GetXml() as XmlElement;

            // Add signature to element
            elem.AppendChild(xmlDoc.ImportNode(xmlDigitalSignature, true)); // https://msdn.microsoft.com/en-us/library/ms229745%28v=vs.110%29.aspx
            //            elem.AppendChild(xmlDigitalSignature);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="xmlDoc"></param>
        public void ValidateSignature(XmlDocument xmlDoc)
        {
            XmlNamespaceManager xmlns = new XmlNamespaceManager(xmlDoc.NameTable);
            xmlns.AddNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
            xmlns.AddNamespace("pkif", "http://danskebank.dk/PKI/PKIFactoryService");
            xmlns.AddNamespace("elem", "http://danskebank.dk/PKI/PKIFactoryService/elements");

            // Find the signature node
            var signatureNode = xmlDoc.SelectNodes("//ds:Signature", xmlns).Cast<XmlNode>().SingleOrDefault() as XmlElement;

            if (signatureNode == null)
                throw new Exception("Response is not signed."); // This request is not signed even though we expect _all_ requests to be signed


            var signedNode = signatureNode.ParentNode as XmlElement;

            signedNode.RemoveChild(signatureNode);
            /*
             * Reload only the part of the document which was signed
             */
            xmlDoc = new XmlDocument();
            xmlDoc.PreserveWhitespace = true;
            xmlDoc.LoadXml(signedNode.OuterXml);

            // Validate signature as normal.  
            System.Security.Cryptography.Xml.SignedXml signedXml = new SignedXmlWithId(xmlDoc);
            signedXml.LoadXml(signatureNode);

            /*
             * The actual signature validation is multipart
            *  1) Ensure that a valid signature is present
             *  2) Ensure that the certificate used for the signature is expected (e.g. that we've gotten it previously from the bank)
             *      a) GetBankCertificate - signing cert is the root certificate
             *      b) Other - signing cert is obtained by the request in a)
             *  3) The signing certificate is at most 1 level away from the bank root certificate
             *      Remember: 
             *          Bank root -> signing cert. 
             *          Bank root -> bank CA cert_n -> (customer encryption cert, customer signing cert)
             *          We do not want to allow the latter to be part of the trust chain for responses (It would allow one customer to sign responses to another on behalf of the bank)
             * 
             * 
             */
            {
                var signatureReferences = signedXml.Signature.SignedInfo.References.Cast<Reference>().ToArray();
                if (signatureReferences.Count() != 1)
                    throw new Exception("Invalid number of signature references");

                /*
                 * According to "PKI service description v2.1", section 6.8.1 "References and id-attributes in signatures" the responses all have a
                 * xml:id="response" identifier, so we can use the explicit xml-prefix to perform the lookup
                 * 
                 * For security reasons, we only search for a matching ID in the root node.
                 *  - xml dsig allows an Any node in the signature
                 *  - An attacker could move the signed response into the signature and replace the response with a malicionsly crafted one
                 *  - Without the "top level only"-match we could validate the now-moved response element, and then return the attacker-crafted 
                 *    to the user 
                 *    
                 * Therefore, we ensure that signedNode is the sole reference from the signature
                 * (signedNode was read as signatureNode.Parent above)
                 */
                if (signatureReferences.First().Uri != "#" + signedNode.Attributes["xml:id"].Value)
                    throw new Exception("Signature reference does not represent a correctly enveloped signature");
            }

            // Check signature using the expected certificate
            if (!signedXml.CheckSignature())
                throw new Exception("Invalid signature on response");

            var responseCerts = signedXml.Signature.KeyInfo.Cast<KeyInfoX509Data>().First().Certificates;
            if (responseCerts.Count != 1)
                throw new Exception("Multiple signature certificates present");

            var responseSignCert = responseCerts[0] as X509Certificate2;

            #region Check the certificate chain
            var chain = new X509Chain();
            chain.ChainPolicy.ExtraStore.Add(_bankRootCertificate);

            // FIXME: Revocation checks of the entire chain should be done against the CRL published by the bank.
            //        This list is obtainable given a link in the bank root certificate
            chain.ChainPolicy.RevocationMode = X509RevocationMode.NoCheck; // Do not check if the certificate has been revoked
            chain.ChainPolicy.VerificationFlags = X509VerificationFlags.AllowUnknownCertificateAuthority;  // Allow that the root cert (DBGROOT) is not present in the local certificate store
            chain.ChainPolicy.RevocationFlag = X509RevocationFlag.EntireChain;

            // Do the validation.
            var isValid = chain.Build(responseSignCert);
            if (!isValid)
                throw new Exception("Cannot verify signature - Invalid certificate chain");

            // Ensure there is at most 2 elements in the chain (DB Signing cert -> DBGroot)
            if (chain.ChainStatus.Length == 2)
                throw new Exception("Cannot verify signature - certificate chain longer than expected");

            // Ensure that the end of the chain is the known bank root certificate
            if (chain.ChainElements.Cast<X509ChainElement>().Last().Certificate.Thumbprint != _bankRootCertificate.Thumbprint)
                throw new Exception("Cannot verify signature - certificate chain did not end in known root");
            #endregion
        }
    }
}
