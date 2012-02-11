<?php

/**
 * This is the model class for table "ac_users".
 *
 * The followings are the available columns in table 'ac_users':
 * @property integer $ac_user_id
 * @property string $name
 * @property string $login
 * @property string $password
 * @property integer $is_admin
 * @property integer $is_observer
 *
 * The followings are the available model relations:
 */
class AcUser extends CActiveRecord
{
    /**
     * Returns the static model of the specified AR class.
     * @return AcUser the static model class
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
        return 'ac_users';
    }

    /**
     * @return array validation rules for model attributes.
     */
    public function rules()
    {
        // NOTE: you should only define rules for those attributes that
        // will receive user inputs.
        return array(
			array('name, login', 'unique'),
            array('name, login, password', 'required'),
            array('is_admin, is_observer', 'numerical', 'integerOnly'=>true),
            array('name', 'length', 'max'=>255),
            array('login, password', 'length', 'max'=>80),
            // The following rule is used by search().
            // Please remove those attributes that should not be searched.
            array('ac_user_id, name, login, password, is_admin, is_observer', 'safe', 'on'=>'search'),
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
        );
    }

    /**
     * @return array customized attribute labels (name=>label)
     */
    public function attributeLabels()
    {
        return array(
            'ac_user_id' => 'Идентификатор',
            'name' => 'Имя',
            'login' => 'Логин',
            'password' => 'Пароль',
            'is_admin' => 'Администратор',
            'is_observer' => 'Наблюдатель',
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

        $criteria->compare('ac_user_id',$this->ac_user_id);
        $criteria->compare('name',$this->name,true);
        $criteria->compare('login',$this->login,true);
        $criteria->compare('is_admin',$this->is_admin);
        $criteria->compare('is_observer',$this->is_observer);
		
		$criteria->order = 'name';

        return new CActiveDataProvider(get_class($this), array(
            'criteria'=>$criteria
        ));
    }
}