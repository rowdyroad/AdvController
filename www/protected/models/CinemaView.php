<?

class CinemaView extends CActiveRecord
{	
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}

	public function tableName()
	{
		return 'v_cinemas';
	}
	
	public function attributeLabels()
	{
		return array(
			'cinema_id' => 'Идентификатор',
			'cinema' => 'Название',
            'location'=>'Страна/Город'
		);
	}
 }
 ?>