using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;

namespace Registrator.ExternalInteface
{

    public interface IProcessHandler
    {
        void OnProcess(ExternalProcess sender, byte percent);
        void OnComplete(ExternalProcess sender, int exitCode);
        void OnReceive(ExternalProcess sender, string line);
    };

    public class ExternalProcess
    {

        private string program_path_;
        private ProcessStartInfo program_;
        private IProcessHandler handler_ = null;
        public string ProgramPath
        {
            get { return program_path_; }
        }

        public ProcessStartInfo Program
        {
            get { return program_; }
        }

        private String flushResource(byte[] data)
        {
            String fn = System.IO.Path.GetTempPath() + "/" + System.IO.Path.GetRandomFileName();
            FileStream f = File.Create(fn);
            f.Write(data, 0, data.Length);
            f.Close();
            return fn;
        }

        static private ProcessStartInfo getResourceProcessStartInfo(String filename)
        {
            ProcessStartInfo pInfo = new ProcessStartInfo();
            pInfo.CreateNoWindow = true;
            pInfo.RedirectStandardOutput = true;
            pInfo.UseShellExecute = false;
            pInfo.FileName = filename;
            return pInfo;
        }

        ~ExternalProcess()
        {
           if (System.IO.File.Exists(program_path_))
           {
                System.IO.File.Delete(program_path_);
           }           
        }

        public ExternalProcess(byte[] resource)
        {
            program_path_ = flushResource(resource);
            program_ = getResourceProcessStartInfo(program_path_);
        }

        public bool Execute(String arguments, IProcessHandler handler)
        {
            new System.Threading.Thread(delegate() { 
            program_.Arguments = arguments;
            Process p = Process.Start(program_);            
            byte progress;
            while (true)
            {
                String r = p.StandardOutput.ReadLine();
                if (r == null) break;
                if (r[0] == '=')
                {
                    int last = r.IndexOf(' ');

                    if (last == -1) { last = r.Length; };
                    if (byte.TryParse(r.Substring(1, last-1), out progress))
                    {
                        if (handler != null) 
                            handler.OnProcess(this, progress);
                    }
                }
                else
                {
                    if (handler != null) 
                        handler.OnReceive(this, r);
                }
            }
            
            if (handler != null)
                handler.OnComplete(this, p.ExitCode);
            }).Start();

            return true;
        }
    }
}
