using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Registrator.Storage
{
    public interface IStorage
    {
        PromoIdent[] GetPromoList();        
        Boolean RenamePromo(PromoIdent promo, string name)       ;
        Boolean RemovePromo(PromoIdent promo);
        uint GetNewPromoId();
        Boolean Add(uint ident, string name, uint length);
    }
}
