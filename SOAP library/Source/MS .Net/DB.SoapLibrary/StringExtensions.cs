using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary
{
	public static class StringExtensions
	{
		/// <summary>
		/// Test if a string is null, empty or consists of only white-space characters
		/// </summary>
		/// <param name="s"></param>
		/// <returns>false if the string is null, empty, or contains only white-space characters, otherwise it returns true</returns>
		public static bool isSomething(this string s)
		{
			return !string.IsNullOrWhiteSpace(s);
		}
	}
}