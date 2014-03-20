<?php
include './api/class/Utils.php';
//include_once 'api/class/passwordLib.php';
//echo password_hash('qwepoi12', 'pSkSwBMzsg');
//
//$birthday = "12/18/2013";
//$regx = "(((0[13578]|10|12)([-./])(0[1-9]|[12][0-9]|3[01])([-./])(\d{4}))|((0[469]|11)([-./])([0][1-9]|[12][0-9]|30)([-./])(\d{4}))|((2)([-./])(0[1-9]|1[0-9]|2[0-8])([-./])(\d{4}))|((2)(\.|-|\/)(29)([-./])([02468][048]00))|((2)([-./])(29)([-./])([13579][26]00))|((2)([-./])(29)([-./])([0-9][0-9][0][48]))|((2)([-./])(29)([-./])([0-9][0-9][2468][048]))|((2)([-./])(29)([-./])([0-9][0-9][13579][26])))";
//if (preg_match($regx, $birthday))
//{
//    echo "success";
//} else
//{
//    echo "DGJH";
//}
//$array = array();
//for ($i = 0; $i<10; $i++)
//{
//    $id = getToken(10);
//    $lat = getToken(10);
//    $long = getToken(10);
//    $name = getToken(10);
//    $type = getToken(10);
//
//    $array[$i] = array('id' => $id, 'latitude' => $lat, 'longitude' => $long, 'name' => $name, 'type' => $type,);
//}
//$a = array('locals'=>$array);
//echo json_encode($a);
//$time =date("Ymdhis");
//
//echo $time;
//echo date('d.m.Y H:i:s', 1370956788472);
//$d = 1370956788472 / 1000;
//echo date('d.m.Y H:i:s', $d);
//$serverMicrotime = microtime(true);
//echo "<br />" . $serverMicrotime . "<br />";
//$timeDiff = $serverMicrotime - $clientMicrotime;
//if ($timeDiff <= 100.0)
//    echo 'true';
//else
//    echo 'false';

//$json = json_decode('{"ids":[{"id":"id1"},{"id":"id2"},{"id":"id3"},{"id":"id4"}],"username":"user"}',true);
$json = json_decode('[{"id":"id1"},{"id":"id2"},{"id":"id3"},{"id":"id4"}]');
$count = count($json);
echo $count;

for ($i=0; $i<$count; $i++)
{
    foreach($json[$i] as $key=>$value)
    {
        echo $value;
    }
}
//
////
////echo getEmailMessage(getToken(10), "MpApQ");
////
////mail("mario.pedro.queiroz@gmail.com", "subject", getEmailMessage(getToken(10), "MpApQ"));
//echo "ola";
//require './api/class/mail/PHPMailerAutoload.php';
//
//$mail = new PHPMailer;
//
//$mail->isSMTP();                                      // Set mailer to use SMTP
//$mail->Host = 'smtp.gmail.com';  // Specify main and backup server
//$mail->SMTPAuth = true;
//$mail->Port = 465;
//// Enable SMTP authentication
//$mail->Username = 'myvaproducts@gmail.com';                        // SMTP username
//$mail->Password = 'arqamsimov';                          // Enable encryption, 'ssl' also accepted
//$mail->SMTPSecure='ssl';
//
//$mail->From = 'myvaproducts@gmail.com';
//$mail->FromName = 'MyVa';
//$mail->addAddress('mario.pedro.queiroz@gmail.com');
//$mail->addAddress('tiagoqueiroz13@hotmail.com');
//$mail->addAddress('leniker.gomes@gmail.com');
//$mail->addAddress('len-iker@hotmail.com');
//$mail->addAddress('hugommdias_92@hotmail.com');
//$mail->addAddress('tiagofilipealvesqueiros@gmail.com');
//
//// Name is optional
//$mail->isHTML(false);                                  // Set email format to HTML
//
//$mail->Subject = 'MyVa - Sign-Up Confirmation';
//$mail->msgHTML(getEmailMessage(getToken(10), "MpApQ"));
////$mail->Debugoutput = 'echo';
////$mail->SMTPDebug = true;
//if (!$mail->send())
//{
//    echo 'Message could not be sent.';
//    echo 'Mailer Error: ' . $mail->ErrorInfo;
//    exit;
//}
//
//echo 'Message has been sent';
?>
<!--
<html>
    <head>
        <script src="js/jquery-latest.js" type="text/javascript"></script>
        <script src="js/hmac-sha1.js" type="text/javascript"></script>
        
    </head>
    <body>
        <script>
            alert(CryptoJS.HmacSHA1('teste', '123456'));
        </script>
        
    </body>
    
</html>-->