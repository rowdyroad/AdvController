<?

class Country extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
        return 'countries';
    }

    public function rules()
	{
		return array(
			array('name', 'unique'),
			array('name', 'required'),
			array('name', 'length', 'max'=>255));
    }

    public function attributeLabels()
	{
		return array('name' => 'Название');
    }
    
}

?>