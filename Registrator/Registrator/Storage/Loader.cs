using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Registrator.Storage
{
    class Loader
    {
        private System.Net.WebClient client_ = new System.Net.WebClient();   
        private string base_url_;
        public Loader(string url)
        {
            base_url_ = url;
        }
        public  T Load<T>(string command)
        {
            try
            {
                String r = client_.DownloadString(base_url_ + command);
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
