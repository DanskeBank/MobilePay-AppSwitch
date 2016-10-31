using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Db.SoapLibrary.Specification
{
	public class An<TException> where TException : Exception
	{
		public class ExpectedExceptionFailed : Exception
		{
			public ExpectedExceptionFailed(string msg) : base(msg) { }
		}

		public static void ShouldBeThrownBy(Action action)
		{
			try
			{
				action();
				throw new ExpectedExceptionFailed($"expected a {typeof(TException).Name} to be caught but none was thrown");
			}
			catch (ExpectedExceptionFailed) { throw; }
			catch (Exception e)
			{
				if (!typeof(TException).IsAssignableFrom(e.GetType()))
					//if (!e.GetType().IsAssignableFrom(typeof(TException)))
					throw new ExpectedExceptionFailed($"expected an exception of type {typeof(TException).Name} but a {e.GetType().Name} was caught");
			}
		}

		public static void ShouldBeThrownBy<T>(Func<T> action)
		{
			ShouldBeThrownBy(() => { var _ = action(); });
		}
	}

	public class A<TException> : An<TException> where TException : Exception { }
}
