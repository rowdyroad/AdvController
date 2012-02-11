<?

class Event extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
        return 'v_events';
    }


    public function attributeLabels()
	{
		return array(
        'dt_registered'=>'Время события',
        'promo_name' => 'Ролик',
        'cinema'=>'Кинотеатр',
        'hall'=>'Зал',
        'location'=>'Страна/Город',
        'client_name'=>'Заказчик',
        'probability' => 'Веc'    
        );
    }
}

?>