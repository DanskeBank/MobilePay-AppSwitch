using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.GetStatusV02;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.GetStatus.V02
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class GetStatusV02Spec
    {
        [Test]
        public void Can_Send_GetStatusRequest()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "ero2309151";
            var actioncode = "B";

            SoapConnection<GetStatusV02Client, GetStatusV02> soapConnection =
                new SoapConnection<GetStatusV02Client, GetStatusV02>(new EndpointConfiguration().GetStatusV02);

            GetStatusRequest req =
                new GetStatusRequest
                {
                    dacGetStatusInput = new dacGetStatusInput
                    {
                        MerchantId = merchantid,
                        OrderId = orderid,
                        CustomerId = string.Empty,
                        ActionCode = actioncode
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
            var output = soapConnection.Send(client => client.GetStatus(ref req.Security, req.RequestHeader, req.dacGetStatusInput));
            Console.Write(output);
            Assert.IsNotNull(output);
        }
    }
}