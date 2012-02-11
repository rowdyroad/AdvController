<?php

class SoasView extends CActiveRecord
{
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}

	public function tableName()
	{
		return 'v_soas';
	}

	public function attributeLabels()
	{
		return array(
			'soas_id' => 'Идентификатор',
			'cinema' => 'Кинотеатр',
            'last_heart_beat' => 'Время жизни',
            'pc_ident'=>'Идентификатор ПК',
            'hall'=>'Зал',
            'registration_time'=>'Дата и время регистрации',
            'location'=>'Страна/Город',
            'annotation'=>'Описание'
		);
	}    
}