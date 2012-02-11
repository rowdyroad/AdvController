<?php

/**
 * This is the model class for table "cinemas".
 *
 * The followings are the available columns in table 'cinemas':
 * @property integer $cinema_id
 * @property string $name
 * @property string $comments
 *
 * The followings are the available model relations:
 */
class Cinema extends CActiveRecord
{
	/**
	 * Returns the static model of the specified AR class.
	 * @return Cinema the static model class
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
		return 'cinemas';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('name, network_id, city_id, country_id', 'required'),
			array('name', 'length', 'max'=>100),
			array('annotation', 'safe'),
			// The following rule is used by search().
			// Please remove those attributes that should not be searched.
			array('cinema_id, name, comments', 'safe', 'on'=>'search'),
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
			'soas'=>array(self::HAS_MANY, 'Soas', 'cinema_id')
        ); 
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'cinema_id' => 'Идентификатор',
			'name' => 'Название',
			'annotation' => 'Комментарий',
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

		$criteria->compare('cinema_id',$this->cinema_id);
		$criteria->compare('name',$this->name,true);
		$criteria->compare('annotation',$this->comments,true);

		return new CActiveDataProvider(get_class($this), array(
			'criteria'=>$criteria,
		));
	}
}