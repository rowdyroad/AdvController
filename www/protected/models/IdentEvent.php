<?

class IdentEvent extends  Event
{
    public static function model($class = __CLASS__)
    {
        return parent::model($class);
    }

    public function tableName()
    {
      //return 'v_idents_events';
      return 'events';
    }
    
    public function attributeLabels()
    {
	return parent::attributeLabels();        
    }    
}

?>