rm -rf tmp
mkdir tmp
cp -r Projects/yundongjingan_new/assets/* tmp/
coffee -c tmp/config/*.coffee||exit
shopt -s extglob
rm -rf !(tmp/config/*.json|tmp/config/*.js)
cd tmp
zip -r config.zip config javascript webview
cd ..
scp tmp/config.zip 42.62.101.160:/data/www/ruby_general_admin/public/config/
rm -rf tmp
