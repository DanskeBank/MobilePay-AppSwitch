using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.GetStatusV03;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.GetStatus.V03
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class GetStatusV03Spec
    {
        [Test]
        public void Can_Send_GetStatusRequest()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            var actioncode = "E";

            SoapConnection<GetStatusV03Client, GetStatusV03> soapConnection =
                new SoapConnection<GetStatusV03Client, GetStatusV03>(new EndpointConfiguration().GetStatusV03);

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
            Assert.IsNotNull(output);
        }
    }
}