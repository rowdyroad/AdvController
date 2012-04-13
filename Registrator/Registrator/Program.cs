using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;

namespace Registrator
{
    static class Program
    {
        /// <summary>
        /// Главная точка входа для приложения.
        /// </summary>
        [STAThread]
        static void Main()
        {

            ProcessStartInfo wmark,detect;
            try
            {
                wmark = getResourceProcessStartInfo("watermark.ex_", Properties.Resources.watermark);
            }
            catch (Exception e)
            {
                MessageBox.Show("Ошибка при инициализации программы");
                return;
            }

            try
            {
                detect = getResourceProcessStartInfo("detect.ex_", Properties.Resources.detect);
            }
            catch (Exception e)
            {
                MessageBox.Show("Ошибка при инициализации программы");
                return;
            }

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1(wmark,detect));
            removeResource(wmark.FileName);
            removeResource(detect.FileName);
        }

        static private ProcessStartInfo getResourceProcessStartInfo(String filename, byte[] data)
        {
            ProcessStartInfo pInfo = new ProcessStartInfo();
            pInfo.CreateNoWindow = true;
            pInfo.RedirectStandardOutput = true;
            pInfo.UseShellExecute = false;
            pInfo.FileName = flushResource(filename, data);
            return pInfo;
        }
        static private String flushResource(String filename, byte[] data)
        {
            String fn =  "./" + filename;
            FileStream f = File.Create(fn);
            f.Write(data, 0, data.Length);
            f.Close();
            return fn;
        }

        static private void removeResource(String filename)
        {
            if (File.Exists(filename))
            {
                File.Delete(filename);
            }
        }

        

    }
}
