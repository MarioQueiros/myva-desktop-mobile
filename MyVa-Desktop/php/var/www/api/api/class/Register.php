<?php

include_once 'Credentials_DB.php';
include_once 'Utils.php';
include_once 'passwordLib.php';

function register($app)
{
    $request = (array) json_decode($app->request()->getBody());

    $username = $request['username'];
    $password = $request['password'];
    $email = $request['email'];
    $birthday = $request['birthday'];

    $regx = "(((0[13578]|10|12)([-./])(0[1-9]|[12][0-9]|3[01])([-./])(\d{4}))|((0[469]|11)([-./])([0][1-9]|[12][0-9]|30)([-./])(\d{4}))|((2)([-./])(0[1-9]|1[0-9]|2[0-8])([-./])(\d{4}))|((2)(\.|-|\/)(29)([-./])([02468][048]00))|((2)([-./])(29)([-./])([13579][26]00))|((2)([-./])(29)([-./])([0-9][0-9][0][48]))|((2)([-./])(29)([-./])([0-9][0-9][2468][048]))|((2)([-./])(29)([-./])([0-9][0-9][13579][26])))";

//    if (preg_match($regx, $birthday))
//    {
        if (filter_var($email, FILTER_VALIDATE_EMAIL))
        {
            $username = strtolower($username);
            try
            {
                $credentials = new Credentials_DB();
                $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
                $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            } catch (PDOException $ex)
            {
                die(json_encode(array('message' => 'Unable to connect')));
            }

            $stmt = $pdo->prepare('SELECT * FROM Users WHERE Email=?');
            $stmt->execute(array($email));
            //$res = $stmt->fetchAll();

            $row_count = $stmt->rowCount();

            // check if already have email on DB
            if ($row_count == 0)
            {
                $stmt = $pdo->prepare('SELECT * FROM Users WHERE Username=?');
                $stmt->execute(array($username));
                //$res = $stmt->fetchAll();
                $row_count = $stmt->rowCount();

                // check if already have username on DB
                if ($row_count == 0)
                {
                    $pub_key = getToken(32);
                    $priv_key = getToken(10);

                    // save PrivKey
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);
                    foreach ($res[0] as $key => $useless)
                    {
                        $uuidPrivKey = $useless;
                    }

                    $stmt = $pdo->prepare('INSERT INTO PrivKey (ID,PrivKey) values (:id,:privkey)');
                    $stmt->bindParam(':id', $uuidPrivKey);
                    $stmt->bindParam(':privkey', $priv_key);
                    $stmt->execute();

                    //Save PubKey
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);

                    foreach ($res[0] as $key => $useless)
                    {
                        $uuidPubKey = $useless;
                    }

                    $stmt = $pdo->prepare('INSERT INTO PubKey (ID,PubKey) values (:id,:pubkey)');
                    $stmt->bindParam(':id', $uuidPubKey);
                    $stmt->bindParam(':pubkey', $pub_key);
                    $stmt->execute();

                    //Generate Salt+Hash Password
                    $salt = getToken(10);
                    $pass_gen = password_hash($password, $salt);

                    //Save User
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);

                    foreach ($res[0] as $key => $useless)
                    {
                        $uuidUser = $useless;
                    }
                    $reg = 0;
                    $stmt = $pdo->prepare('INSERT INTO Users (ID,Username,Email,Password,Birthday,PrivKey_ID,PubKey_ID,RegConf,Salt) values (:id,:user,:email,:pass,:birth,:priv,:pub,:reg,:salt)');
                    $stmt->bindParam(':id', $uuidUser);
                    $stmt->bindParam(':user', $username);
                    $stmt->bindParam(':email', $email);
                    $stmt->bindParam(':pass', $pass_gen);
                    $stmt->bindParam(':birth', $birthday);
                    $stmt->bindParam(':pub', $uuidPubKey);
                    $stmt->bindParam(':priv', $uuidPrivKey);
                    $stmt->bindParam(':reg', $reg);
                    $stmt->bindParam(':salt', $salt);
                    $stmt->execute();

                    sendEmail($email, "MyVa - Sign-Up Confirmation", $priv_key, $username);

                    //Set response
                    $response = array('SignUp' => 'Success');
                    
                    $app->response()->header('Content-Type', 'application/json');
                    $app->response()->header('X-Status-Reason', json_encode(array('SignUp' => 'Success')));
                    
                    echo json_encode($response);
                } else
                {
                    $app->response()->status(401);
                    $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username already registered')));
                }
            } else
            {
                $app->response()->status(401);
                $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Email already registered')));
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'The email address is not valid')));
        }
    /*}else 
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'The birthday is in incorrect format')));
    }*/
}
