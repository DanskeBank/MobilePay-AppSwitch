using DB.SoapLibrary;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Db.SoapLibrary.Specification
{
	public class ControllableTimeProvider : ITimeProvider
	{
		DateTime now = DateTime.Now;

		public DateTime Now
		{
			get { return now; }
			set { now = value; }
		}
	}
}
