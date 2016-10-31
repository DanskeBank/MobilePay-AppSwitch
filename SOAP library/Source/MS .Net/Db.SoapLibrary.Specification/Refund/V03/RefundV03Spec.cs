using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.RefundV03;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Refund.V03
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class RefundV03Spec
    {
        [Test]
        public void Can_Send_Refund_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            decimal amount = 105;

            SoapConnection<RefundV03Client, RefundV03> soapConnection = new SoapConnection<RefundV03Client, RefundV03>(new EndpointConfiguration().RefundV03);

            var req = new RefundRequest
            {
                dacRefund_Input = new dacRefund_Input
                {
                    Amount = amount,
                    CustomerId = string.Empty,
                    MerchantId = merchantid,
                    OrderId = orderid,
                    Test = "N",
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
                soapConnection.Send(client => client.Refund(ref req.Security, req.RequestHeader, req.dacRefund_Input));
            Assert.IsNotNull(output);
        }
    }
}
