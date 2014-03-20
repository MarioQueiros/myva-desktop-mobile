<?php

include_once 'Credentials_DB.php';

function getPublicLocals($app)
{
    $request = (array) json_decode($app->request()->getBody());
    $trim = $request['search'];
    $search = trim($trim);

    if ($search != "")
    {
        try
        {
            $credentials = new Credentials_DB();
            $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $ex)
        {
            die(json_encode(array('message' => 'Unable to connect')));
        }

        $stmt = $pdo->prepare('SELECT ID,Latitude,Longitude,Name,Type_ID FROM Local WHERE Local LIKE \'%:search%\'');
        $stmt->bindParam(':search', $search);
        $stmt->execute();
        $a = array();
        if ($stmt->rowCount() > 0)
        {
            $i=0;
            foreach ($stmt->fetch(PDO::FETCH_ASSOC) as $row)
            {
                $id = $row['ID'];
                $lat = $row['Latitude'];
                $long = $row['Longitude'];
                $name = $row['Name'];
                $type = $row['Type_ID'];

                $a[$i] = array('id' => $id, 'latitude' => $lat, 'longitude' => $long, 'name' => $name, 'type' => $type,);
                $i++;
            }

            $array = array('locals' => $a);

            echo json_encode($array);
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Search query return 0 results')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Search query empty')));
    }
}
