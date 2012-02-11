<?

class Client extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
	   return parent::model($class);
    }
    
    public function tableName()
    {
    	return 'clients';
    }
    
    public function rules()
	{
		return array(
			array('name', 'unique'),
			array('name', 'required'),
			array('name', 'length', 'max'=>255),
            array('about','safe')
            );
    }
    
    public function attributeLabels()
	{
		return array('name' => 'Название','about'=>'Информация');
    }
}

?>