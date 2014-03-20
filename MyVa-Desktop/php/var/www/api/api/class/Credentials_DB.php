<?php

class Credentials_DB
{

    var $PASSWORD;
    var $USER;
    var $DATABASE;
    var $SERVER;
    var $DSN;

    function Credentials_DB()
    {
        $this->PASSWORD = "arqamsimov";
        $this->USER = 'root';
        $this->DATABASE = 'MyVa';
        $this->SERVER = 'localhost';
        $this->DSN = 'mysql:dbname=' . $this->DATABASE . ';host=' . $this->SERVER;
    }

    public function getPASSWORD()
    {
        return $this->PASSWORD;
    }

    public function getUSER()
    {
        return $this->USER;
    }

    public function getDATABASE()
    {
        return $this->DATABASE;
    }

    public function getSERVER()
    {
        return $this->SERVER;
    }

    public function getDSN()
    {
        return $this->DSN;
    }

}
