using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    public abstract class Marker
       : IProcessHandler
       , IMarker 
    {
        private IMarkHandler handler_;
        private ExternalProcess process_;

        public IMarkHandler Handler
        {
            get { return handler_; }
        }

        protected abstract String getAttributes(string filename, uint mark, string destination, uint offset);

        protected Marker(byte[] resource)           
        {
            process_ = new ExternalProcess(resource);
        }

        public void OnProcess(ExternalProcess sender, byte percent)
        {
            handler_.OnMarkProcess(this, percent);
        }

        public void OnComplete(ExternalProcess sender, int exitCode)
        {
            handler_.OnMarkComplete(this, exitCode);
        }
        public bool Mark(string filename, uint mark, string destination, uint offset, IMarkHandler markHandler)
        {
            handler_ = markHandler;
            return process_.Execute(getAttributes(filename, mark,destination, offset), this);
        }

        public void OnReceive(ExternalProcess sender, string line)
        {
            
        }
    }
}
