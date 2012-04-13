 using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.Storage
{
    public class PromoIdent
    {
        private uint ident_;
        private uint id_;
        private string name_;
        private uint length_;

        public uint Ident {
            get { return ident_; }
            set { ident_ = value; }
        } 
        public uint Id {
            get { return id_; }
            set { id_ = value; }        
        }
        public string Name 
        {
            get { return name_; }
            set { name_ = value; }
        }
        public uint Length {
            get { return length_; }
            set { length_ = value; }   
        }

        public override string ToString()
        {
            return String.Format("{0}    {1}",name_,ident_);
        }
    }
}
