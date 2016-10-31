using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.CancelV02;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Cancel.V02
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class CancelV02Spec
    {
        readonly EndpointConfiguration endpoints = new EndpointConfiguration();
        
        [Test]
        public void Can_Send_Cancel_V02_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            SoapConnection<CancelV02Client, CancelV02> soapConnection =
                new SoapConnection<CancelV02Client, CancelV02>(endpoints.CancelV02);

            CancelRequest req =
                new CancelRequest
                {
                    Input = new Input
                    {
                        MerchantID = merchantid,
                        OrderID = orderid,
                        CustomerId = string.Empty,
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
                        RequestId = "123",
                        Timestamp = DateTime.UtcNow.DbTimestamp()
                    }
                };

            var output = soapConnection.Send(client => client.Cancel(ref req.Security, req.RequestHeader, req.Input));
            Assert.IsNotNull(output);
        }
    }
}
