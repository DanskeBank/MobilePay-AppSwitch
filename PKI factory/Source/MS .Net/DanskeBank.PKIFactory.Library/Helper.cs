using Org.BouncyCastle.Math;

namespace DanskeBank.PKIFactory.Library
{
    /// <summary>
    /// Helper class for common utility methods.
    /// </summary>
    public static class Helper
    {
        /// <summary>
        /// Converts hexadecimal string to deciman representation.
        /// </summary>
        /// <param name="hexStr">Hexadecimal string</param>
        /// <returns>Decimal string</returns>
        public static string HexToDec(string hexStr)
        {
            return new BigInteger(hexStr, 16).ToString();
        }
    }
}
