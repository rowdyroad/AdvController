using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator
{
    public class PromoIdent
    {
        private int ident_;
        private int id_;
        private string name_;
        private int length_;
        private string add_time_;
        private int actived_;

        public int Ident {
            get { return ident_; }
            set { ident_ = value; }
        }
        public int Id {
            get { return id_; }
            set { id_ = value; }        
        }
        public string Name 
        {
            get { return name_; }
            set { name_ = value; }
        }
        public int Length {
            get { return length_; }
            set { length_ = value; }   
        }

        public int Actived
        {
            get { return actived_; }
            set { actived_ = value; }
        }
        public string Add_time
        {
            get { return add_time_; }
            set { add_time_ = value; }   
        }

        public override string ToString()
        {
            return name_;
        }
    }
}
