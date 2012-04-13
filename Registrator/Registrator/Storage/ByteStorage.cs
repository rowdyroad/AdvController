using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.Storage
{
    public class ByteStorage
        :  IStorage
    {
        private Loader loader_;

        public ByteStorage()
        {
            loader_ = new Loader("http://incinema.rowdyro.com/index.php?r=remoting/");
        }

        public PromoIdent[] GetPromoList()
        {
            PromoIdentList idents = loader_.Load<PromoIdentList>("idents");
           if (idents.result == "success")
           {
               return idents.data;
           }
           else
           {
               return null;
           }

        }

        public Boolean RenamePromo(PromoIdent promo, string name)
        {
            Result res = loader_.Load<Result>("rename&id=" + promo.Id + "&name=" + name);
            if (res.result == "success")
            {
                promo.Name = name;
                return true;
            }
            return false;            
        }

        public Boolean RemovePromo(PromoIdent promo)
        {
            Result res = loader_.Load<Result>("remove&id=" + promo.Id);
            if (res.result == "success")
            {
                return true;
            }
            return false;
        }

        public uint GetNewPromoId()
        {
            IdentResult r = loader_.Load<IdentResult>("getident");
            if (r.result == "success")
            {               
                return r.data.ident;
            }
            return 0;
        }



        public bool Add(uint ident, string name, uint length)
        {
            Result m = loader_.Load<Result>("add&ident=" + ident + "&length=" + length + "&name=" + System.Web.HttpUtility.UrlEncode(name));
            if (m.result == "success")
            {
                return true;
            }
            return false;
        }
    }
}
