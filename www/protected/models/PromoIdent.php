<?php

/**
 * This is the model class for table "promos".
 *
 * The followings are the available columns in table 'promos':
 * @property integer $promo_id
 * @property string $dt_added
 * @property string $name
 * @property string $comments
 * @property string $dt_deleted
 *
 * The followings are the available model relations:
 */
class PromoIdent extends CActiveRecord
{
	/**
	 * @var string Содержит имя файла ролика. Используется при создании записи,
	 * в базе не сохраняется. 
	 */
	public $file;
	
	/**
	 * Returns the static model of the specified AR class.
	 * @return Promo the static model class
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
		return 'promo_idents';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('name', 'unique'),
			array('name', 'required'),
			array('name', 'length', 'max'=>255),
			array('ident', 'unique'),
			array('ident', 'required'),
			array('ident', 'length', 'max'=>255),
			array('add_time','safe')
		);
	}



	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'ident' => 'Идентификатор',            
			'add_added' => 'Дата добавления',
			'name' => 'Имя',
		);
	}
	public function search()
	{
	    return new CActiveDataProvider(get_class($this));
	}
}