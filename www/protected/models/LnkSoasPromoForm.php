<?php

/**
 * Модель формы привязки роликов к серверам.
 */
class LnkSoasPromoForm extends CFormModel
{
	public $soas_id;
	public $promo_id;
	public $detail_ids = array();

	/**
	 * Declares the validation rules.
	 * The rules state that username and password are required,
	 * and password needs to be authenticated.
	 */
	public function rules()
	{
		return array(
			array('soas_id', 'required', 'on' => 'promo'),
			array('promo_id', 'required', 'on' => 'soas'),
			array('soas_id, promo_id', 'numerical'),
			array('detail_ids', 'safe')
		);
	}
	
	public function createLinks($targetIdField, $detailIdField)
	{
		$detailId = null;
		
		$sql = "INSERT INTO lnk_soas_promo(soas_id, promo_id) VALUES(:soas_id, :promo_id)";
		
		$command = Yii::app()->db->createCommand($sql);
		$command->bindValue(':'.$targetIdField, $this[$targetIdField], PDO::PARAM_INT);
		$command->bindParam(':'.$detailIdField, $detailId, PDO::PARAM_INT);
		
		foreach ($this->detail_ids as $detailId => $value)
			if ($value == 1) $command->execute();
		
        $r = Yii::app()->db->createCommand("select lnk.*,soas.min_frequency, soas.max_frequency,soas.kill_gate
                                         from lnk_soas_promo as lnk, soas where lnk.sha1 ='' and lnk.soas_id = soas.soas_id")->queryAll();           
        
        $dr = $_SERVER['DOCUMENT_ROOT'];
        foreach ($r as $item)
        {
            extract($item);
            system(Yii::app()->params['capturer']." -f $min_frequency -F $max_frequency -k $kill_gate -p $dr/files/$promo_id.wav,$dr/finger_prints/$lnk_soas_promo_id,$promo_id >> convert.log  2>&1",$ret);
            
            if ($ret != 0)
            {               
                Yii::app()->db->createCommand("delete from lnk_soas_promo where lnk_soas_promo_id = $lnk_soas_promo_id")->execute();
            }
            else
            {                                
                if ($sha1 = sha1_file("{$dr}/finger_prints/{$lnk_soas_promo_id}"))
                {                   
                    Yii::app()->db->createCommand("update lnk_soas_promo set sha1='$sha1' where lnk_soas_promo_id='$lnk_soas_promo_id'")->execute();
                }
                else        
                {                
                    Yii::app()->db->createCommand("delete from lnk_soas_promo where lnk_soas_promo_id = $lnk_soas_promo_id")->execute();
                }                
            }                     
        }
            
		return true;
	}
}
