JavaEE7+WicketでWebアプリケーションを開発するためのMaven Archetypeです.

組み込みGlassFishを使うことで、Mavenさえあれば開発可能な環境を提供しています.  
もちろんEclipseでの開発が可能です. 必要なプラグインがほとんど無いので、開発環境の構築が簡単です.  

# 前提
[Maven](https://maven.apache.org)3系を導入している必要があります.


# プロジェクトの作成

mvnコマンドを使います.
下記はコマンド例です.

```
mvn -B archetype:generate -DgroupId=sandbox -DartifactId=Sandbox -DarchetypeGroupId=info.jabara -DarchetypeArtifactId=archetype-javaee7-wicket -DarchetypeVersion=1.0.0 -DarchetypeCatalog=http://jabaraster.github.io/maven/
```

groupId,artifactIdは適宜変更して下さい.

# Eclipseで開発するために

プロジェクトディレクトリにcdした上で、下記コマンドを実行します.

```
mvn eclipse:eclipse
```

これで、Eclipseプロジェクトに対応されますので、Eclipseから当プロジェクトをインポートして下さい.

# Webアプリの実行

## Eclipseで起動する場合
src/test/javaソースフォルダ下にある ``` sandbox.web.container.JavaEEContainerStarter ``` クラスを起動して下さい.

## Mavenで起動する場合
下記コマンドを実行します.

```
mvn test-compile exec:java
```

Webアプリには下記URLでアクセス出来ます.

```
http://localhost:8081/
```

なお各種設定はGlassFish4系へのデプロイを念頭に設定されています.  

# プロジェクト構成や設定の解説

## DB関連

### 開発環境用DBについて
開発環境では、H2 Database Engineを組み込みモードで利用します.  
このため、RDBを別途インストールする必要がありません.  

H2のデータベースファイルは、デフォルトでプロジェクト直下の ``` target/db/ ``` の中に作られます.  
このディレクトリは、環境変数、またはJVM起動引数で変更出来ます.  

### JDBCリソースについて

``` jdbc/App ``` という名前のJDBCリソースを使ってJPAからDBアクセスするように設定しています.  
本番環境ではこの名前でJDBCリソースを作っておけば、ソースコードに手を加える必要がありません.  

### スキーマの自動作成について

デフォルトでは、Webアプリケーション起動時に、JPAのエンティティクラスに従って自動でテーブルを作成する設定になっています.  
２回目以降の起動では、テーブルは再作成されません.  
エンティティ定義とスキーマに齟齬が生じた場合は、データベースファイルを削除して下さい.  
これにより次回起動時にテーブルが再作成されます.  


