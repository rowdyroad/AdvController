<?

class Fake extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
	return parent::model($class);
    }

    public function tableName()
    {
        return 'fakes';
    }

    public function rules()
	{
		return array(
			array('soas_id,fake_soas_id', 'required'),
            array('soas_id,fake_soas_id','numerical')
            );
    }
};

?>