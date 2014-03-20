<?php

include_once 'Credentials_DB.php';

function getAllEvents($app)
{
    $username = $app->request()->headers('X-USERNAME');

    $pubKey = $app->request()->headers('X-PUBKEY');

    try
    {
        $credentials = new Credentials_DB();
        $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $ex)
    {
        die(json_encode(array('message' => 'Unable to connect')));
    }

    $stmt = $pdo->prepare('SELECT PubKey_ID,ID FROM Users WHERE Username=:user');
    $stmt->bindParam(':user', $username);
    $stmt->execute();
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($stmt->rowCount() != 0)
    {
        $userID = $row['ID'];

        $stmt = $pdo->prepare('SELECT PubKey FROM PubKey WHERE ID=:pub_key');
        $stmt->bindParam(':pub_key', $row['PubKey_ID']);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($pubKey == $row['PubKey'])
        {
            $stmt = $pdo->prepare('SELECT ID,Local_ID,Name,TimestampEvent,DateEvent,Open FROM Event WHERE User_ID=:id ORDER BY DateEvent ASC');
            $stmt->bindParam(':id', $userID);
            $stmt->execute();

            if ($stmt->rowCount() > 0)
            {
                $a = array();
                $i = 0;
                $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
                foreach ($rows as $row)
                {
                    $id = $row['ID'];
                    $local = $row['Local_ID'];
                    $name = utf8_encode($row['Name']);
                    $timestamp = $row['TimestampEvent'];
                    $date = $row['DateEvent'];
                    $open = $row['Open'];

		    if($open == 1){
			$a[$i] = array('id' => $id, 'local' => $local, 'name' => $name, 'timestamp' => $timestamp, 'dateevent' => $date, 'userid' => $userID);
			$i++;
		    }
                }
                $array = array('events' => $a);

		$app->response()->header('Content-Type', 'application/json');
                $app->response()->header('X-Status-Reason', json_encode($array));

                echo json_encode($array);
            } else
            {
                $app->response()->status(401);
                $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'The user doesn\'t have any event saved')));
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to get events from other user')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
    }
}
