using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Registrator
{
    static public class Loader
    {
        private static System.Net.WebClient client_ = new System.Net.WebClient();        
        static public  T Load<T>(string command)
        {
            try
            {
                String r = client_.DownloadString("http://srs-mngr.dyndns.org/index.php?r=remoting/" + command);
                return Newtonsoft.Json.JsonConvert.DeserializeObject<T>(r);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Ошибка доступа к серверу", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return Newtonsoft.Json.JsonConvert.DeserializeObject<T>("");
            }
        }
    }
}
