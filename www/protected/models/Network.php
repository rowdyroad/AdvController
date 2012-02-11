<?

class Network extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
        return 'networks';
    }

    public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
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