using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.ReserveV01;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Reserve.V01
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class ReserveV01Spec
    {
        [Test]
        public void Can_Send_Reserve_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            decimal amount = 105;

            SoapConnection<ReserveV01Client, ReserveV01> soapConnection = 
                new SoapConnection<ReserveV01Client, ReserveV01>(new EndpointConfiguration().ReserveV01);

            var req = new ReserveRequest
            {
                dacInput = new dacInput
                {
                    MerchantId = merchantid,
                    CustomerId = string.Empty,
                    OrderId = orderid,
                    CardChecksum = string.Empty,
                    Amount = amount,
                    MinimumAmount = amount,
                    Partial = "N",
                    Message = string.Empty,
                    Test = "Y",
                    UseDefaultCard = string.Empty,
                    BulkRef = string.Empty,                    
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
                soapConnection.Send(client => client.Reserve(ref req.Security, req.RequestHeader, req.dacInput));
            Assert.IsNotNull(output);
        }
    }
}
