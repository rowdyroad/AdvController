<?

class Utils
{
    static private $months = array('января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря');
    static private $short_months = array('янв', 'фев', 'мар', 'апр', 'мая', 'июн', 'июл', 'авг', 'сен', 'окт', 'ноя', 'дек');
    static private $days = array('Вс','Пн','Вт','Ср','Чт','Пт','Сб');
    
    function DateTimeFromTS($timestamp) 
    {
        return date('d',$timestamp).' '.(self::$short_months[date("m",$timestamp) - 1]).' '.date('Y H:i:s',$timestamp).' '.self::$days[date('w',$timestamp)];
    }
    
    function DateFromTS($timestamp)
    {
        return date('d',$timestamp).' '.(self::$months[date("m",$timestamp) - 1]).' '.date('Y',$timestamp);
    }
    
    function ShortDateFromTs($timestamp)
    {
        return date('d',$timestamp).'&nbsp;'.(self::$short_months[date("m",$timestamp) - 1]);        
    }
        
    
}

?>