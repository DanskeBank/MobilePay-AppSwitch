using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.RequestPaymentV01;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.RequestPayment.V01
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class RequestPaymentV01Spec
    {
        [Test]
        public void Can_Send_RequestPayment_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            decimal amount = 105;

            SoapConnection<RequestPaymentV01Client, RequestPaymentV01> soapConnection = 
                new SoapConnection<RequestPaymentV01Client, RequestPaymentV01>(new EndpointConfiguration().RequestPaymentV01);

            var req = new RequestPaymentRequest
            {
                dacRefund_Input = new dacRefund_Input
                {
                    MerchantId = merchantid,
                    Amount = amount,
                    BulkRef = string.Empty,
                    CustomerId = string.Empty,
                    OrderId = orderid,
                },
                Security = new SecurityHeaderType(),
                RequestHeader = new RequestHeaderType
                {
                    Language = "DA",
                    SenderId = senderId,
                    SignerId1 = senderId,
                    SignerId2 = senderId,
                    SignerId3 = senderId,
                    DBCryptId = senderId,
                    RequestId = "1234",
                    Timestamp = DateTime.UtcNow.DbTimestamp()
                }
            };

            var output =
                soapConnection.Send(client => client.RequestPayment(ref req.Security, req.RequestHeader, req.dacRefund_Input));
            Assert.IsNotNull(output);
        }
    }
}
