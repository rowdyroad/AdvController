<?php

/**
 * This is the model class for table "soas".
 *
 * The followings are the available columns in table 'soas':
 * @property integer $soas_id
 * @property integer $cinema_id
 * @property string $name
 * @property string $identity
 * @property string $comments
 * @property string $dt_deleted
 *
 * The followings are the available model relations:
 */
class Soas extends CActiveRecord
{
	/**
	 * Returns the static model of the specified AR class.
	 * @return Soas the static model class
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
		return 'soas';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
            array('soas_id,hall','numerical','integerOnly'=>true),
			array('cinema_id', 'numerical', 'integerOnly'=>true),
            array('hall,cinema_id','required'),
			array('annotation, pc_ident', 'safe')
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		return array(
			'cinema'=>array(self::BELONGS_TO, 'Cinema', 'cinema_id')
        ); 
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'soas_id' => 'Идентификатор',
			'cinema_id' => 'Кинотеатр',
			'annotation' => 'Описание',
            'last_heart_beat' => 'Время жизни',
            'pc_ident'=>'Идентификатор ПК',
            'hall'=>'Зал',
            'registration_time'=>'Дата и время регистрации'
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
		$criteria->compare('comments',$this->comments,true);
		$criteria->compare('cinema_id',$this->cinema_id,true);		
		$criteria->order = 'name';

		return new CActiveDataProvider(get_class($this), array(
			'criteria'=>$criteria,
		));
	}
	
	public static function getIdByIdentity($identity)
	{
		$command = Yii::app()->db->createCommand('SELECT soas_id FROM soas WHERE identity=:identity');
		$command->bindValue(':identity', $identity, PDO::PARAM_STR);
		$res = $command->queryRow();
		
		if ($res === null) return null;
		
		return $res['soas_id'];
	}
}