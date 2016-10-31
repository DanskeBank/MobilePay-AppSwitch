using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Db.SoapLibrary.Specification
{
	public static class TimeSpanExtensions
	{
		public static TimeSpan seconds(this int nSeconds) { return new TimeSpan(0, 0, nSeconds); }
		public static TimeSpan minutes(this int nMinutes) { return new TimeSpan(0, nMinutes, 0); }
	}
}
