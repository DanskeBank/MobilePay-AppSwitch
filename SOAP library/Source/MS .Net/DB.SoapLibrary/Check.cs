using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary
{
	public class Check
	{
		bool condition;

		private Check(bool condition) { this.condition = condition; }

		public static Check that(bool condition) { return new Check(condition); }

		public void otherwiseThrow(Exception e) { if (!condition) throw e; }

		public void otherwise(Action onConditionFailure) { if (!condition) onConditionFailure(); }
	}
}
