using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary
{
	public class SystemTimeProvider : ITimeProvider
	{
		public DateTime Now { get { return DateTime.Now; } }
	}
}
