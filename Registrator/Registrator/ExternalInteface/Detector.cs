using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    public abstract class Detector
        : IProcessHandler
        , IDetector 
    {
        private IDetectHandler handler_;
        private ExternalProcess process_;

        public IDetectHandler Handler
        {
            get { return handler_; }
        }

        protected abstract String getAttributes(string filename, uint offset);

        protected Detector(byte[] resource)            
        {
            process_ = new ExternalProcess(resource);
        }

        public void OnProcess(ExternalProcess sender, byte percent)
        {
            handler_.OnDetectProcess(this, percent);
        }

        public void OnComplete(ExternalProcess sender, int exitCode)
        {
            handler_.OnDetectComplete(this, exitCode);
        }

        public void Detect(string filename, uint offset, IDetectHandler detectHandler)
        {
            handler_ = detectHandler;
            process_.Execute(getAttributes(filename,offset), this);
        }

        public void OnReceive(ExternalProcess sender, string line)
        {
            if (line[0] == '+')
            {                

                byte mark;
                int pos = line.LastIndexOf(' ');
                if (byte.TryParse(line.Substring(pos + 1,line.Length - (pos + 1)), out mark))
                {                   
                    this.notifyDetectByte(mark);
                }
            }
        }

        abstract protected void notifyDetectByte(byte mark);
    }
}
