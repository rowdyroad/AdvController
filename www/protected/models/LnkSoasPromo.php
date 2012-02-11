<?php

/**
 * This is the model class for table "lnk_soas_promo".
 *
 * The followings are the available columns in table 'lnk_soas_promo':
 * @property integer $lnk_soas_promo_id
 * @property integer $soas_id
 * @property integer $promo_id
 *
 * The followings are the available model relations:
 */
class LnkSoasPromo extends CActiveRecord
{
	/**
	 * Returns the static model of the specified AR class.
	 * @return LnkSoasPromo the static model class
	 */
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}

	/**
	 * @return string the associated database table name
	 */
	public function tableName()
	{
		return 'lnk_soas_promo';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('soas_id, promo_id', 'required'),
			array('soas_id, promo_id', 'numerical', 'integerOnly'=>true),
			// The following rule is used by search().
			// Please remove those attributes that should not be searched.
			array('lnk_soas_promo_id, soas_id, promo_id', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		return array(
			'soas'=>array(self::BELONGS_TO, 'Soas', 'soas_id'),
			'promo'=>array(self::BELONGS_TO, 'Promo', 'promo_id')
        ); 
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'lnk_soas_promo_id' => 'Lnk Soas Promo',
			'soas_id' => 'Soas',
			'promo_id' => 'Promo',
		);
	}

	/**
	 * Retrieves a list of models based on the current search/filter conditions.
	 * @return CActiveDataProvider the data provider that can return the models based on the search/filter conditions.
	 */
	public function search()
	{
		// Warning: Please modify the following code to remove attributes that
		// should not be searched.

		$criteria=new CDbCriteria;

		$criteria->compare('soas_id',$this->soas_id);
		$criteria->compare('promo_id',$this->promo_id);

		return new CActiveDataProvider(get_class($this), array(
			'criteria'=>$criteria,
		));
	}
	
	public static function deleteBySoasId($id)
	{
		$command = Yii::app()->db->createCommand('DELETE FROM lnk_soas_promo WHERE soas_id=:soas_id');
		$command->bindValue(':soas_id', $id, PDO::PARAM_INT);
		$command->execute();
	}
	
	public static function deleteByPromoId($id)
	{
		$command = Yii::app()->db->createCommand('DELETE FROM lnk_soas_promo WHERE promo_id=:promo_id');
		$command->bindValue(':promo_id', $id, PDO::PARAM_INT);
		$command->execute();
	}
	
	static public function getPromoIdsForSoas($soas_id)
	{
		$command = Yii::app()->db->createCommand('
			SELECT
				promos.*
			FROM
				lnk_soas_promo,
                promos
            WHERE 
                lnk_soas_promo.soas_id = :soas_id and promos.promo_id = lnk_soas_promo.promo_id');
		$command->bindValue(':soas_id', $soas_id, PDO::PARAM_STR);
		return $command->queryAll();
	}
	
	static public function checkIfLinkExists($soasIdentity, $promoId)
	{
		$command = Yii::app()->db->createCommand('
			SELECT
				lnk_soas_promo.soas_id
			FROM
				lnk_soas_promo
			INNER JOIN soas ON ( soas.soas_id = lnk_soas_promo.soas_id AND soas.identity=:identity)
			WHERE
				lnk_soas_promo.promo_id=:promo_id');
		$command->bindValue(':identity', $soasIdentity, PDO::PARAM_STR);
		$command->bindValue(':promo_id', $promoId, PDO::PARAM_STR);
		
		$res = $command->queryRow();
		
		if ($res === null) return null;
		
		return $res['soas_id'];
	}
}