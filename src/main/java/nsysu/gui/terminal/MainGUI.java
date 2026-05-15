package nsysu.gui.terminal;

import nsysu.bank.account.BasicAccount;
import nsysu.bank.role.Administrator;
import nsysu.bank.role.Person;
import nsysu.bank.role.User;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.SameUserNameException;
import nsysu.util.exception.TargetNotFindException;

import java.util.List;
import java.util.Scanner;

public class MainGUI {
    static Scanner scanner= new Scanner(System.in);
    static Person person;
    static int action;
    public static void main(String[] args) {
        while (true){
            System.out.print("UserName: ");
            String name = scanner.next();
            System.out.print("Password: ");
            String password = scanner.next();
            try{
                person = Person.logIn(name,password);
                break;
            }
            catch (TargetNotFindException e){
                System.out.print("UserName or Password error");
            }
        }
        while (true){
            System.out.println("You are "+person.getRole());
            System.out.println("You want to: (select number)");
            if(person instanceof Administrator administrator){
                System.out.println("1. add new user");
                System.out.println("2. deactivate user");
                System.out.println("3. give user money");
                System.out.println("4. unfreeze account");
                System.out.println("5. search user's account");
                System.out.println("0. exit");
                action = scanner.nextInt();
                if (action==0){
                    System.out.println("Exit");
                    break;
                }
                switch (action){
                    case 1:
                        while(true){
                            System.out.print("UserName: ");
                            String name = scanner.next();
                            System.out.print("Password: ");
                            String password = scanner.next();
                            try {
                                System.out.println("Success to create new user: "+administrator.addNewUser(name,password));
                                break;
                            }
                            catch (SameUserNameException e){
                                System.out.println("The user have existed");
                            }
                        }
                        break;
                    case 2:
                        while(true){
                            System.out.print("UserName: ");
                            String name = scanner.next();
                            try {
                                if(administrator.deactivateUser(name)){
                                    System.out.println("Success to deactivate "+name);
                                }
                                else{
                                    System.out.println("Fail to deactivate "+name);
                                }
                                break;
                            }
                            catch (TargetNotFindException e){
                                System.out.println("User name not find");
                            }
                        }
                        break;
                    case 3:
                        while(true){
                            System.out.print("accountId: ");
                            String accountId = scanner.next();
                            System.out.print("value: ");
                            double value = scanner.nextDouble();
                            try {
                                if(administrator.giveMoney(accountId,value)){
                                    System.out.println("Success to give "+accountId+" "+value);
                                }
                                else{
                                    System.out.println("amount can't be negative");
                                }
                                break;
                            }
                            catch (IdNotFindException e){
                                System.out.println("account not find");
                            }
                        }
                        break;
                    case 4:
                        while(true){
                            System.out.print("accountId: ");
                            String accountId = scanner.next();
                            try {
                                if(administrator.unfreezeAccount(accountId)){
                                    System.out.println("Success to unfreeze "+accountId);
                                }
                                else{
                                    System.out.println("error! the account isn't frozen");
                                }
                                break;
                            }
                            catch (TargetNotFindException e){
                                System.out.println("User name not find");
                            }
                        }
                        break;
                    case 5:
                        while(true){
                            System.out.print("userName: ");
                            String name = scanner.next();
                            try {
                                List<BasicAccount> accounts =  administrator.getAccountByUserName(name);
                                System.out.println("number of account: "+accounts.size());
                                for(BasicAccount account : accounts){
                                    System.out.println(account.toString());
                                }
                                break;
                            }
                            catch (TargetNotFindException e){
                                System.out.println("User name not find");
                            }
                        }
                        break;
                    default:
                        System.out.println("I don't know what you want");
                        break;
                }
            }
            else if(person instanceof User user){
                System.out.println("1. add new account");
                System.out.println("2. query account");
                System.out.println("3. query total balance");
                System.out.println("4. transfer to your account");
                System.out.println("5. transfer to other's account");
                System.out.println("6. withdraw/deposit");
                System.out.println("7. close account");
                action = scanner.nextInt();
                if (action==0){
                    System.out.println("Exit");
                    break;
                }
//                for(BasicAccount account : user.getAccount()){
//
//                }
            }
        }
    }
}
