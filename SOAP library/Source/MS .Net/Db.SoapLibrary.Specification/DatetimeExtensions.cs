using System;

namespace Db.SoapLibrary.Specification
{
    public static class DatetimeExtensions
    {
        public static string DbTimestamp(this DateTime date)
        {
            return date.ToString("yyyy-MM-ddTHH:mm:ssZ");
        }
    }
}
