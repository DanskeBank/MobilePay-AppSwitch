# Content
<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Content](#content)
- [About](#about)
- [Prerequisites](#prerequisites)
- [Setting Key Vault Privileges](#setting-key-vault-privileges)
- [Certificates](#certificates)
- [Adding Certificates](#adding-certificates)
- [Adding Certificate Password](#adding-certificate-password)
- [Scripting Certificate Management With PowerShell](#scripting-certificate-management-with-powershell)

<!-- /TOC -->
# About
This document describes how to upload certificates, needed by the soap library, to the azure key vault.

# Prerequisites
*   A key vault is created and configured. In this document we use the key vault `mptest-keyvault` as an example.
*	It is assumed that the reader has installed and configured the [azure powershell cmdlets](https://azure.microsoft.com/da-dk/documentation/articles/powershell-install-configure/).
*	Furthermore the reader should be familiar with the [azure key vault cmdlets](https://msdn.microsoft.com/en-us/library/dn868052.aspx).
*	It is also assumed that the reader has an account on Danske Banks azure, and can log in to [azure portal](https://portal.azure.com) as well as [MobilePay Test's Azure Active Directory](https://manage.windowsazure.com/mobilepaytest.onmicrosoft.com#Workspaces/ActiveDirectoryExtension/Directory/900750e7-f71e-486d-957f-7fead99489ec/directoryDashboard), and has the credentials to manage applications there.
*	The reader must also be able to log in from a powershell using the cmdlet `Login-AzureRmAccount`.

# Setting Key Vault Privileges
*   Open a powershell.
*   Log on to azure
        `Login-AzureRmAccount`
*   Assuming you have the user name 'abc@danskebank.dk', grant privilege to manage keys and secrets by executing the following command
        `Set-AzureRmKeyVaultAccessPolicy -VaultName "mptest-keyvault" -UserPrincipalName 'abc@danskebank.dk'  -PermissionsToKeys all -PermissionsToSecrets all`

Assuming no errors were encountered, you now have the privilege to manage keys and secrets in the key vault.

# Certificates
To communicate with soap service gateway you will need 4 certificates: two client certificates and two bank certificates; One for encryption and one for signing, for both client and bank certificates. The client certificates contains both the public and the private key. The bank certificates, on the other hand, contain only the public key.

All certificates must be DER encoded binary X.509 and then base-64 encoded, since the key vault only accepts strings as secrets.

Note: all certificates are stored as *secrets*. Otherwise soap library would have to be mostly rewritten in order for it to use the key vaults facilities for encrypting, decrypting, signing and verification.

# Adding Certificates
First we must load the contents of a certificate from disk (change the file name, vault name and certificate name to match your requirements):

	$fileContentBytes = [System.IO.File]::ReadAllBytes(<<cert-file-name>>)

Then we must base-64 encode it so we get a string:

	$fileContentEncoded = [System.Convert]::ToBase64String($fileContentBytes)

Next step is to convert it to a secure string:

	$secret_string = ConvertTo-SecureString -String $fileContentEncoded -AsPlainText -Force

And finally we add that to the key vault:

	$secret = Set-AzureKeyVaultSecret -VaultName <<vault-name>> -Name <<certificate-name>> -SecretValue $secret_string

# Adding Certificate Password
In order for the soap library to use the client certificates it must also have the password to those. This ensures the following:

*   The passwords are not stored in a configuration file, but are protected by the key vault.
*   A (future) certificate renewal service will be able to automatically renew certificates that are in danger of expiring.

The naming convention is: `<<certificate-name>> + '-password'`. So if your client encryption certificate is named 'SYST-AppSwitch-Crypt' then the password must be named 'SYST-AppSwitch-Crypt-password' in the key vault.

First store the password in a secure string

    $password = 'abc123'
    $secret_string = ConvertTo-SecureString -String $password -AsPlainText -Force

Then store the password in azures key vault

    $secret = Set-AzureKeyVaultSecret -VaultName mptest-keyvault -Name 'SYST-AppSwitch-Crypt-password' -SecretValue $secret_string

# Scripting Certificate Management With PowerShell
The following powershell script is an example of how to add or update your certificates. Note that you have to log in first using `Login-AzureRmAccount`.
```
	# -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	# This script adds/updates client- and bank certificates in azure's key vault.
	#
	# Change the names of the certfiticates, files, and vault to match your requirements.
	# Place the script together with your certificate files and run it.
	# -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	$clientEncryptionCertName = 'SYST-AppSwitch-Crypt'
	$clientSignatureCertName = 'SYST-AppSwitch-Sign'
    $clientEncryptionCertPassword = 'Qwe123123'
    $clientSignatureCertPassword = 'Qwe123123'
	$bankEncryptionCertName = 'SYST-AppSwitch-DPCRYPT'
	$bankSignatureCertName = 'SYST-AppSwitch-DPSIGN'
	$clientEncryptionFileName = '4F4741_crypt.pfx'
	$clientSignatureFileName = '4F4741_sign.pfx'
	$bankEncryptionFileName = 'DBGCrypt.crt'
	$bankSignatureFileName = 'DBGSign.crt'
	$vaultName = 'mptest-keyvault'

	function SetCertificate($fileName, $secretName)
	{
		$filePath = [System.IO.Path]::Combine((get-location).path,$fileName)
		$fileContentBytes = [System.IO.File]::ReadAllBytes($filePath)
		$fileContentEncoded = [System.Convert]::ToBase64String($fileContentBytes)
		$secret_string = ConvertTo-SecureString -String $fileContentEncoded -AsPlainText -Force
		$secret = Set-AzureKeyVaultSecret -VaultName $vaultName -Name $secretName -SecretValue $secret_string
	}

    function SetCertificatePassword($certificateName, $password)
    {
        $secretName = $certificateName + '-password'
        $secret_string = ConvertTo-SecureString -String $password -AsPlainText -Force
        $secret = Set-AzureKeyVaultSecret -VaultName $vaultName -Name $secretName -SecretValue $secret_string
    }

	# -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	# Update certificates
	# -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	SetCertificate $clientEncryptionFileName $clientEncryptionCertName
	SetCertificate $clientSignatureFileName $clientSignatureCertName
	SetCertificate $bankEncryptionFileName $bankEncryptionCertName
	SetCertificate $bankSignatureFileName $bankSignatureCertName

    # -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    # Update client certificate passwords
    # -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    SetCertificatePassword $clientEncryptionCertName $clientEncryptionCertPassword
    SetCertificatePassword $clientSignatureCertName $clientSignatureCertPassword
```
