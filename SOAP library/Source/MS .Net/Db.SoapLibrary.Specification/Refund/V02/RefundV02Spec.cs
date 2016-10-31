using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.RefundV02;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Refund.V02
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class RefundV02Spec
    {
        [Test]
        public void Can_Send_Refund_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
            decimal amount = 105;

            SoapConnection<RefundV02Client, RefundV02>
                soapConnection = new SoapConnection<RefundV02Client, RefundV02>(new EndpointConfiguration().RefundV02);

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
