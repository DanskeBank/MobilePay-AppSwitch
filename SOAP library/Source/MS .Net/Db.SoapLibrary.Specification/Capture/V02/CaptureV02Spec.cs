﻿using System;
using DB.SoapLibrary;
using DB.SoapLibrary.Specification.CaptureV02;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.Capture.V02
{
    [TestFixture]
    [Category("IntegrationTests")]
    public class CaptureV02Spec
    {
        [Test]
        public void Can_Send_Capture_Request()
        {
            var senderId = "061133";
            var merchantid = "APPDK0074110008";
            var orderid = "DB TESTING 2015060908";
   
            SoapConnection<CaptureV02Client, CaptureV02> soapConnection =
                new SoapConnection<CaptureV02Client, CaptureV02>(new EndpointConfiguration().CaptureV02);

            var req = new CaptureRequest
            {
                Input = new Input
                {
                    MerchantId = merchantid,
                    Amount = 102,
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

            var output = soapConnection.Send(client => client.Capture(ref req.Security, req.RequestHeader, req.Input));
            Assert.IsNotNull(output);
        }
    }
}
