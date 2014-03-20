<?php

include_once 'mail/class.phpmailer.php';

function crypto_rand_secure($min, $max)
{
    $range = $max - $min;
    if ($range < 0)
    {
        return $min; // not so random...
    }
    $log = log($range, 2);
    $bytes = (int) ($log / 8) + 1; // length in bytes
    $bits = (int) $log + 1; // length in bits
    $filter = (int) (1 << $bits) - 1; // set all lower bits to 1
    do
    {
        $rnd = hexdec(bin2hex(openssl_random_pseudo_bytes($bytes)));
        $rnd = $rnd & $filter; // discard irrelevant bits
    } while ($rnd >= $range);
    return $min + $rnd;
}

function getToken($length)
{
    $token = "";
    $codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    $codeAlphabet.= "abcdefghijklmnopqrstuvwxyz";
    $codeAlphabet.= "0123456789";
    for ($i = 0; $i < $length; $i++)
    {
        $token .= $codeAlphabet[crypto_rand_secure(0, strlen($codeAlphabet))];
    }
    return $token;
}

function getEmailMessage($priv_key, $username)
{
    $message = '<img src="http://mpapq.myftp.org/api/images/myva.png" /><br /><br />Hello <b>' . $username . '</b>,<br />Thanks for register on <b>MyVa</b> products.<br /><br />Please insert the following code on the requested field to confirm your account:<br /><br /><br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>' . $priv_key . '</b><br /><br /><br />Thank you.<br /><b>MyVa Team</b>';
    return $message;
}

function getResendEmailMessage($priv_key, $username)
{
    $message = '<img src="http://mpapq.myftp.org/api/images/myva.png" /><br /><br />Hello <b>' . $username . '</b>,<br />Thanks for register on <b>MyVa</b> products.<br /><br />Please insert the following code on the requested field to confirm your account on your device:<br /><br /><br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>' . $priv_key . '</b><br /><br /><br />Thank you.<br /><b>MyVa Team</b>';
    return $message;
}

function sendEmail($email, $subject, $token, $username)
{
    $mail = new PHPMailer;

    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Port = 465;
    $mail->Username = 'myvaproducts@gmail.com';
    $mail->Password = 'arqamsimov';
    $mail->SMTPSecure = 'ssl';
    $mail->From = 'myvaproducts@gmail.com';
    $mail->FromName = 'MyVa';
    $mail->addAddress($email);
    $mail->isHTML(false);
    $mail->Subject = $subject;
    $mail->msgHTML(getEmailMessage($token, $username));

    if (!$mail->send())
    {
        return;
    }
}

function resendEmail($email, $subject, $token, $username)
{
    $mail = new PHPMailer;

    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Port = 465;
    $mail->Username = 'myvaproducts@gmail.com';
    $mail->Password = 'arqamsimov';
    $mail->SMTPSecure = 'ssl';
    $mail->From = 'myvaproducts@gmail.com';
    $mail->FromName = 'MyVa';
    $mail->addAddress($email);
    $mail->isHTML(false);
    $mail->Subject = $subject;
    $mail->msgHTML(getResendEmailMessage($token, $username));

    if (!$mail->send())
    {
        return;
    }
}
