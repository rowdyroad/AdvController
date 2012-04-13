using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.ExternalInteface
{
    public interface IMarkHandler
    {
        void OnMarkProcess(IMarker sender, byte percent);
        void OnMarkComplete(IMarker sender, int errno);
    }
    public interface IMarker
    {
        bool Mark(string filename, uint mark, string destination, uint offset, IMarkHandler markHandler);       
    }

}
