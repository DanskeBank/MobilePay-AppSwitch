using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.SendV01;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Send.V01
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class SendV01Spec
    {
        [Test]
        public void Can_Send_Send_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            decimal amount = 105;

            SoapConnection<SendV01Client, SendV01> soapConnection = 
                new SoapConnection<SendV01Client, SendV01>(new EndpointConfiguration().SendV01);

            var req = new SendRequest
            {
                dacInput = new dacInput
                {
                    MerchantId = merchantid,
                    CustomerId = string.Empty,
                    OrderId = orderid,
                    BulkRef = string.Empty,
                    Amount = amount,
                    Message = string.Empty,
                    ActionCode = "V",
                    Test = "Y",
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
                soapConnection.Send(client => client.Send(ref req.Security, req.RequestHeader, req.dacInput));
            Assert.IsNotNull(output);
        }
    }
}
