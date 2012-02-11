<?php

/**
 * This is the model class for table "promo_registration_events".
 *
 * The followings are the available columns in table 'promo_registration_events':
 * @property integer $promo_registration_event_id
 * @property string $dt_registered
 * @property integer $soas_id
 * @property integer $promo_id
 *
 * The followings are the available model relations:
 */
class PromoRegistrationIdentEvent extends CActiveRecord
{
	/**
	 * Returns the static model of the specified AR class.
	 * @return PromoRegistrationEvent the static model class
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
		return 'promo_idents_events';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('dt_registered,soas_id,promo_ident,probability', 'required'),
			array('soas_id,promo_ident,probability', 'numerical','integerOnly'=>true),
			// The following rule is used by search().
			// Please remove those attributes that should not be searched.
		//	array('promo_registration_event_id, dt_registered, soas_id, promo_ident', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		// NOTE: you may need to adjust the relation name and the related
		// class name for the relations automatically generated below.
		return array(
			'soas'=>array(self::BELONGS_TO, 'Soas', 'soas_id'),
			'promo'=>array(self::BELONGS_TO, 'Promo', 'promo_ident')
        );
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'promo_registration_event_id' => 'Ид. события',
			'dt_registered' => 'Дата',
			'soas_id' => 'Cервер',
			'promo_ident' => 'Ролик',
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
		$criteria->compare('dt_registered',$this->dt_registered,true);
		$criteria->compare('soas_id',$this->soas_id);
		$criteria->compare('promo_ident',$this->promo_ident);
		//$criteria->condition = 'promo_id IN (SELECT promo_id FROM promos)';
		$criteria->order = 'dt_registered DESC';
		return new CActiveDataProvider(get_class($this), array(
			'criteria'=>$criteria,
		));
	}
}