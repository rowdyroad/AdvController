<?

class City extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
	return parent::model($class);
    }

    public function tableName()
    {
        return 'cities';
    }

    public function rules()
	{
		return array(
			array('name', 'required'),
			array('name', 'length', 'max'=>255),
			array('country_id', 'required'),
            array('timezone','required')

            );
    }

    public function attributeLabels()
	{
		return array('name' => 'Название','timezone'=>'Часовой пояс');
    }
}

?>