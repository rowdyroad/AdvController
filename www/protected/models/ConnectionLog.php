<?

class ConnectionLog extends CActiveRecord
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
        return 'connections_log';
    }


    public function attributeLabels()
	{
		return array(
	            'time'=>'Врем�',
	            'remote_addr' => 'IP'
        );
    }
}

?>