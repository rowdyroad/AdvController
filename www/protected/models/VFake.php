<?

class VFake extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
	return parent::model($class);
    }

    public function tableName()
    {
        return 'v_fakes';
    }
    
    public function primaryKey()
    {
	return 'fake_id';
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