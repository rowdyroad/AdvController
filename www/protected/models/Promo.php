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
class Promo extends CActiveRecord
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
		return 'promos';
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
			array('dt_added, comments, dt_deleted', 'safe'),
            array('client_id','safe'),
			// The following rule is used by search().
			// Please remove those attributes that should not be searched.
			array('promo_id, dt_added, name, comments, dt_deleted', 'safe', 'on'=>'search'),
			array('file', 'file', 'allowEmpty'=>true),
		);
	}



	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'promo_id' => 'Идентификатор',
			'dt_added' => 'Дата добавления',
			'name' => 'Имя',
            'sha1' =>'Контрольная сумма',
			'comments' => 'Комментарий',
			'dt_deleted' => 'Дата удаления',
			'file' => 'Файл',
		);
	}

    public function relations()
	{
		return array(
			'client'=>array(self::BELONGS_TO, 'Client', 'client_id')
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

		$criteria->compare('promo_id',$this->promo_id);
		$criteria->compare('dt_added',$this->dt_added,true);
		$criteria->compare('name',$this->name,true);
		$criteria->compare('comments',$this->comments,true);
		$criteria->compare('dt_deleted',$this->dt_deleted,true);		
		$criteria->order = 'name';

		return new CActiveDataProvider(get_class($this), array(
			'criteria'=>$criteria
		));
	}
}